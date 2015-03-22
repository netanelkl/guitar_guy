using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    class CThrdFinger : CBasicGuitarFinger
    {
        public CThrdFinger(CGuitarFinger ThrdFinger)
            : base(ThrdFinger)
        {

        }

        public CThrdFinger(List<CGuitarFinger> ThrdFinger)
            : base(ThrdFinger)
        {

        }

        public CThrdFinger()
            : base()
        {

        }

        public override int GetIndex()
        {
            return 3;
        }

        protected override bool GetPriorityForPlay(CFingerPosition cFngr, bool fCapo,out int nPriority)
        {
            nPriority = 0;
            if (!cFngr.IsValid())
            {
                return false;
            }

            int nResult = 0;
            nResult += (cFngr.Fret * 3);

            if (fCapo)
            {
                nResult -= 10; // TODO: Change to be constants.
            }

            nPriority = nResult;
            return true;
        }

        public override bool CanPlay(CGuitarFinger cFngr)
        {
            if (cFngr.Strings.Count > 1)
            {
                if (cFngr.Strings.Contains(EString.Sixth) ||
                    cFngr.Strings.Contains(EString.Fifth) ||
                    cFngr.Strings.Contains(EString.First))
                {
                    return false;
                }
            }

            return base.CanPlay(cFngr);
        }
    };
}
